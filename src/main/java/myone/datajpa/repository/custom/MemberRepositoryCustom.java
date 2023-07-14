package myone.datajpa.repository.custom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import myone.datajpa.dto.MemberDto;
import myone.datajpa.entity.Member;

public interface MemberRepositoryCustom {
	List<Member> findAllCustom();
	List<MemberDto> findAllJdbcTemplate();
	List<MemberDto> findAllJdbcTemplateSort(Sort sort);
	Page<MemberDto> findAllPageable(Pageable pageable);
}
