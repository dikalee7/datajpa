package myone.datajpa.repository.custom;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import lombok.RequiredArgsConstructor;
import myone.datajpa.dto.MemberDto;
import myone.datajpa.entity.Member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
	
	private final EntityManager em;
	private final JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Member> findAllCustom() {
		// TODO Auto-generated method stub
		return em.createQuery("select m From Member m left join fetch m.team", Member.class).getResultList();
	}

	@Override
	public List<MemberDto> findAllJdbcTemplate() {
		StringBuffer query = new StringBuffer();
        query.append(" select");
        query.append("  m.member_id as member_id,");
        query.append("  m.username as username,");
        query.append("  m.age as age,");
        query.append("  t.name as team_name");
        query.append(" from member m");
        query.append(" left outer join team t");
        query.append("    on m.team_id=t.team_id");
        query.append(" ORDER BY member_id");
		return this.jdbcTemplate.query(query.toString(), memberRowMapper());
		
	}
	
	private RowMapper<MemberDto> memberRowMapper() {
        return (rs, rowNum) -> {
        	MemberDto member = new MemberDto(Long.parseLong(rs.getString("member_id")), rs.getString("username"), rs.getInt("age"), rs.getString("team_name")); 
        	return member;
        };
    }

}