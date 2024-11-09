package elhanchir.mohamed.ebankingbackend.sec.repositories;

import elhanchir.mohamed.ebankingbackend.sec.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
