package com.devchen.acount.dal.repo;

import com.devchen.acount.dal.entity.AccountEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends CrudRepository<AccountEntity, String> {

    AccountEntity findByUsername(String username);
}
