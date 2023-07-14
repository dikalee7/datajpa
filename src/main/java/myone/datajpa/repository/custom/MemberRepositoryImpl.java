package myone.datajpa.repository.custom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.jdbc.core.JdbcTemplate;

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
		return this.jdbcTemplate.query(query.toString(), (rs, rowNum) -> memberRowMapper(rs));
		
	}
	
	private MemberDto memberRowMapper(final ResultSet  rs) throws NumberFormatException, SQLException {
        	MemberDto member = new MemberDto(Long.parseLong(rs.getString("member_id")), rs.getString("username"), rs.getInt("age"), rs.getString("team_name")); 
        	return member;
    }

	@Override
	public List<MemberDto> findAllJdbcTemplateSort(Sort sort) {
		StringBuffer query = new StringBuffer();
		query.append(" select");
		query.append("  m.member_id as member_id,");
		query.append("  m.username as username,");
		query.append("  m.age as age,");
		query.append("  t.name as team_name");
		query.append(" from member m");
		query.append(" left outer join team t");
		query.append("    on m.team_id=t.team_id");

		if (!sort.isUnsorted()) {
			query.append(" order by ");

			for (Iterator<Order> iterator = sort.toList().iterator(); iterator.hasNext();) {
				Order order = (Order) iterator.next();
				query.append(" " + order.getProperty() + " " + order.getDirection().name());
				if (iterator.hasNext())
					query.append(", ");
			}
		}

		return this.jdbcTemplate.query(query.toString(),
				(rs, rowNum) -> new MemberDto(Long.parseLong(rs.getString("member_id")), rs.getString("username"),
						rs.getInt("age"), rs.getString("team_name")));
	}

	@Override
	public Page<MemberDto> findAllPageable(Pageable page) {
		StringBuffer query = new StringBuffer();
		query.append(" select");
		query.append("  m.member_id as member_id,");
		query.append("  m.username as username,");
		query.append("  m.age as age,");
		query.append("  t.name as team_name");
		query.append(" from member m");
		query.append(" left outer join team t");
		query.append("    on m.team_id=t.team_id");
		
		Sort sort = page.getSort();
		if (!sort.isUnsorted()) {
			query.append(" order by ");

			for (Iterator<Order> iterator = sort.toList().iterator(); iterator.hasNext();) {
				Order order = (Order) iterator.next();
				query.append(" " + order.getProperty() + " " + order.getDirection().name());
				if (iterator.hasNext())
					query.append(", ");
			}
		}
		query.append(" LIMIT " + page.getPageSize() + " OFFSET " + page.getOffset());
		
		
		List<MemberDto> members = jdbcTemplate.query(query.toString(),
				(rs, rowNum) -> new MemberDto(Long.parseLong(rs.getString("member_id")), rs.getString("username"),
						rs.getInt("age"), rs.getString("team_name")));
		
	    return new PageImpl<MemberDto>(members, page, jdbcTemplate.queryForObject("select count(*) from member", Integer.class));
	}
}
