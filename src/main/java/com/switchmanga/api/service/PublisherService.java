package com.switchmanga.api.service;

import com.switchmanga.api.entity.Publisher;
import com.switchmanga.api.entity.User;
import com.switchmanga.api.entity.UserRole;
import com.switchmanga.api.repository.PublisherRepository;
import com.switchmanga.api.repository.VolumeRepository;
import com.switchmanga.api.repository.OrderRepository;
import com.switchmanga.api.dto.publisher.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Publisher Service
 * - PUBLIC API: 인증 불필요
 * - ADMIN API: PublisherController에서 사용
 * - PUBLISHER API: PublisherPortalController에서 사용
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublisherService {
    
    private final PublisherRepository publisherRepository;
    private final SeriesRepository seriesRepository;
    private final VolumeRepository volumeRepository;
    private final OrderRepository orderRepository;
    
    // ========================================
    // 🔓 PUBLIC API (인증 불필요)
    // ========================================
    
    /**
     * 모든 출판사 조회 (Public)
     * 활성화된 출판사만 반환
     */
    public List<PublisherInfoResponse> getAllPublishersPublic() {
        return publisherRepository.findAll().stream()
                .filter(p -> Boolean.TRUE.equals(p.getActive()))  // 활성화된 출판사만
                .map(PublisherInfoResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 출판사 조회 (Public)
     * 활성화된 출판사만 조회 가능
     */
    public PublisherInfoResponse getPublisherByIdPublic(Long publisherId) {
        Publisher publisher = findPublisherOrThrow(publisherId);
        
        if (!Boolean.TRUE.equals(publisher.getActive())) {
            throw new RuntimeException("활성화되지 않은 출판사입니다: " + publisherId);
        }
        
        return PublisherInfoResponse.from(publisher);
    }
    
    // ========================================
    // 🔒 ADMIN 전용 메서드 (PublisherController용)
    // ========================================
    
    /**
     * 모든 출판사 조회 (ADMIN 전용)
     * 비활성화된 출판사 포함
     */
    public List<PublisherInfoResponse> getAllPublishers(User admin) {
        validateAdminRole(admin);
        
        return publisherRepository.findAll().stream()
                .map(PublisherInfoResponse::from)
                .collect(Collectors.toList());
    }
    
    /**
     * 특정 출판사 조회 (ADMIN 전용)
     */
    public PublisherInfoResponse getPublisherById(User admin, Long publisherId) {
        validateAdminRole(admin);
        
        Publisher publisher = findPublisherOrThrow(publisherId);
        return PublisherInfoResponse.from(publisher);
    }
    
    /**
     * 출판사 생성 (ADMIN 전용)
     */
    @Transactional
    public PublisherInfoResponse createPublisher(User admin, PublisherCreateRequest request) {
        validateAdminRole(admin);
        
        Publisher publisher = Publisher.builder()
                .name(request.getName())
                .nameEn(request.getNameEn())
                .nameJp(request.getNameJp())
                .country(request.getCountry())
                .email(request.getEmail())
                .phone(request.getPhone())
                .website(request.getWebsite())
                .description(request.getDescription())
                .active(true)
                .build();
        
        Publisher saved = publisherRepository.save(publisher);
        return PublisherInfoResponse.from(saved);
    }
    
    /**
     * 출판사 수정 (ADMIN 전용)
     */
    @Transactional
    public PublisherInfoResponse updatePublisherByAdmin(User admin, Long publisherId, PublisherUpdateRequest request) {
        validateAdminRole(admin);
        
        Publisher publisher = findPublisherOrThrow(publisherId);
        updatePublisherFields(publisher, request);
        
        return PublisherInfoResponse.from(publisher);
    }
    
    /**
     * 출판사 삭제 (ADMIN 전용) - Soft Delete
     */
    @Transactional
    public void deletePublisher(User admin, Long publisherId) {
        validateAdminRole(admin);
        
        Publisher publisher = findPublisherOrThrow(publisherId);
        publisher.setActive(false);
    }
    
    // ========================================
    // 🔒 PUBLISHER 전용 메서드 (PublisherPortalController용)
    // ========================================
    
    /**
     * 내 출판사 정보 조회 (PUBLISHER용)
     * User의 publisher 관계 사용
     */
    public PublisherInfoResponse getMyPublisher(User user) {
        validatePublisherRole(user);
        
        // User에 연결된 Publisher 가져오기
        Publisher publisher = user.getPublisher();
        if (publisher == null) {
            throw new RuntimeException("연결된 출판사가 없습니다. 관리자에게 문의하세요.");
        }
        
        return PublisherInfoResponse.from(publisher);
    }
    
    /**
     * 내 출판사 정보 수정 (PUBLISHER용)
     */
    @Transactional
    public PublisherInfoResponse updateMyPublisher(User user, PublisherUpdateRequest request) {
        validatePublisherRole(user);
        
        Publisher publisher = user.getPublisher();
        if (publisher == null) {
            throw new RuntimeException("연결된 출판사가 없습니다.");
        }
        
        updatePublisherFields(publisher, request);
        
        return PublisherInfoResponse.from(publisher);
    }
    
    /**
     * 내 출판사 통계 조회 (PUBLISHER용)
     */
    public PublisherStatsResponse getMyStats(User user) {
        validatePublisherRole(user);
        
        Publisher publisher = user.getPublisher();
        if (publisher == null) {
            throw new RuntimeException("연결된 출판사가 없습니다.");
        }
        
        // 통계 계산 - Repository 메서드 사용
        Long totalSeries = seriesRepository.countByPublisherId(publisher.getId());
        Long totalVolumes = volumeRepository.countByPublisherId(publisher.getId());
        
        // Order 통계는 복잡한 연관관계 쿼리 필요 - 일단 0으로 설정
        // TODO: 나중에 OrderRepository에 커스텀 쿼리 메서드 추가
        Long totalOrders = 0L;
        
        return PublisherStatsResponse.builder()
                .publisherId(publisher.getId())
                .publisherName(publisher.getName())
                .totalSeries(totalSeries != null ? totalSeries : 0L)
                .totalVolumes(totalVolumes != null ? totalVolumes : 0L)
                .totalOrders(totalOrders)  // 항상 0L이므로 조건 불필요
                .build();
    }
    
    // ========================================
    // 🔹 공통 유틸 메서드
    // ========================================
    
    /**
     * Publisher 조회 (없으면 예외)
     */
    private Publisher findPublisherOrThrow(Long publisherId) {
        return publisherRepository.findById(publisherId)
                .orElseThrow(() -> new RuntimeException("출판사를 찾을 수 없습니다: " + publisherId));
    }
    
    /**
     * Publisher 필드 업데이트
     * country는 최초 등록 시에만 설정 (수정 불가)
     */
    private void updatePublisherFields(Publisher publisher, PublisherUpdateRequest request) {
        if (request.getName() != null) {
            publisher.setName(request.getName());
        }
        if (request.getNameEn() != null) {
            publisher.setNameEn(request.getNameEn());
        }
        if (request.getNameJp() != null) {
            publisher.setNameJp(request.getNameJp());
        }
        // country 업데이트 제거 - PublisherUpdateRequest에 필드 없음
        if (request.getEmail() != null) {
            publisher.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            publisher.setPhone(request.getPhone());
        }
        if (request.getWebsite() != null) {
            publisher.setWebsite(request.getWebsite());
        }
        if (request.getDescription() != null) {
            publisher.setDescription(request.getDescription());
        }
    }
    
    /**
     * ADMIN 권한 검증
     * UserRole enum 사용
     */
    private void validateAdminRole(User user) {
        if (user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("관리자 권한이 필요합니다.");
        }
    }
    
    /**
     * PUBLISHER 권한 검증
     * UserRole enum 사용
     */
    private void validatePublisherRole(User user) {
        if (user.getRole() != UserRole.PUBLISHER && user.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("출판사 권한이 필요합니다.");
        }
    }
    
    /**
     * Publisher 접근 권한 검증 (범용)
     * - ADMIN: 모든 Publisher 접근 가능
     * - PUBLISHER: 자기 Publisher만 접근 가능
     */
    public void validatePublisherAccess(User user, Long publisherId) {
        // ADMIN은 모든 Publisher 접근 가능
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }
        
        // PUBLISHER는 자기 Publisher만 접근
        Publisher userPublisher = user.getPublisher();
        if (userPublisher == null || !userPublisher.getId().equals(publisherId)) {
            throw new RuntimeException("해당 출판사에 접근할 권한이 없습니다.");
        }
    }
}
