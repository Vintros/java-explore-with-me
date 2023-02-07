package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.RequestDto;
import ru.practicum.model.ResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.lang.Long;

@Repository
@Transactional
public interface StatsRepository extends JpaRepository<RequestDto, Long> {

    @Query(value = "select new ResponseDto(r.app, r.uri, count(r.id)) from RequestDto r " +
            "where r.uri in :uris " +
            "and r.timestamp between :start and :end " +
            "group by r.app, r.uri " +
            "order by count(r.id) DESC")
    List<ResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "select new ResponseDto(r.app, r.uri, count(r.id)) from RequestDto r " +
            "where r.ip in (select distinct(i.ip) from RequestDto i group by i.ip) " +
            "and r.uri in :uris " +
            "and r.timestamp between :start and :end " +
            "group by r.app, r.uri " +
            "order by count(r.id) DESC")
    List<ResponseDto> getStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

}
