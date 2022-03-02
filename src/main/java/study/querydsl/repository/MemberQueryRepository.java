package study.querydsl.repository;

import static org.springframework.util.StringUtils.hasText;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.dto.QMemberTeamDto;

//query가 복잡하거나 화면에 종속적인 기능이면 분리하는 것도 좋은 방법
//custom에 얽메일 필요 없다.
@Repository
public class MemberQueryRepository {

	private final JPAQueryFactory queryFactory;

	public MemberQueryRepository(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}
	
	// 회원명, 팀명, 나이(ageGoe, ageLoe)
	public List<MemberTeamDto> search(MemberSearchCondition condition) {
		return queryFactory.select(new QMemberTeamDto(member.id, member.username, member.age, team.id, team.name))
				.from(member).leftJoin(member.team, team)
				.where(usernameEq(condition.getUsername()), teamNameEq(condition.getTeamName()),
						ageGoe(condition.getAgeGoe()), ageLoe(condition.getAgeLoe()))
				.fetch();
	}

	private BooleanExpression usernameEq(String username) {
		return hasText(username) ? member.username.eq(username) : null;
	}

	private BooleanExpression teamNameEq(String teamName) {
		return hasText(teamName) ? team.name.eq(teamName) : null;
	}

	private BooleanExpression ageGoe(Integer ageGoe) {
		return ageGoe != null ? member.age.goe(ageGoe) : null;
	}

	private BooleanExpression ageLoe(Integer ageLoe) {
		return ageLoe != null ? member.age.loe(ageLoe) : null;
	}
}
