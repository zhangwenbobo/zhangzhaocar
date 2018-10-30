package com.zhangzhao.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.zhangzhao.common.entity.Comments;

/**
 * 评论管理
 * 
 * @author Administrator
 *
 */
@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long>, JpaSpecificationExecutor<Comments> {

}
