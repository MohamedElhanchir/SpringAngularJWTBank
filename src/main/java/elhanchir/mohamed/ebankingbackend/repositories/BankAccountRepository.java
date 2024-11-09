package elhanchir.mohamed.ebankingbackend.repositories;

import elhanchir.mohamed.ebankingbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
}
