package co.istad.iptidentityservice.features.role;

import co.istad.iptidentityservice.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
