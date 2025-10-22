package com.switchmanga.api.service;

import com.switchmanga.api.entity.Publisher;
import com.switchmanga.api.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublisherService {

    private final PublisherRepository publisherRepository;

    // 전체 출판사 조회
    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    // 출판사 상세 조회
    public Publisher getPublisherById(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("출판사를 찾을 수 없습니다."));
    }

    // 활성화된 출판사만 조회
    public List<Publisher> getActivePublishers() {
        return publisherRepository.findByActiveTrue();
    }

    // 국가별 출판사 조회
    public List<Publisher> getPublishersByCountry(String country) {
        return publisherRepository.findByCountry(country);
    }

    // 출판사 생성
    @Transactional
    public Publisher createPublisher(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    // 출판사 수정
    @Transactional
    public Publisher updatePublisher(Long id, Publisher publisherDetails) {
        Publisher publisher = getPublisherById(id);

        publisher.setName(publisherDetails.getName());
        publisher.setNameEn(publisherDetails.getNameEn());
        publisher.setNameJp(publisherDetails.getNameJp());
        publisher.setLogo(publisherDetails.getLogo());
        publisher.setCountry(publisherDetails.getCountry());
        publisher.setEmail(publisherDetails.getEmail());
        publisher.setPhone(publisherDetails.getPhone());
        publisher.setWebsite(publisherDetails.getWebsite());
        publisher.setDescription(publisherDetails.getDescription());
        publisher.setActive(publisherDetails.getActive());

        return publisherRepository.save(publisher);
    }

    // 출판사 삭제
    @Transactional
    public void deletePublisher(Long id) {
        Publisher publisher = getPublisherById(id);
        publisherRepository.delete(publisher);
    }
}