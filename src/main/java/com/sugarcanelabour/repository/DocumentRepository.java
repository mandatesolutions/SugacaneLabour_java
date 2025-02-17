package com.sugarcanelabour.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>{

	
	List<Document> findByCommonLogin(CommonLogin commonLogin);

}
