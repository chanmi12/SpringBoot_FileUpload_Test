package com.example.workItem;

import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkRepository extends JdbcParameter<WorkItem,Long> {

}
