package com.pws.admin.repository;

import java.util.List;

import com.pws.admin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pws.admin.entity.Permission;
@Repository

public interface PermissionRepository extends JpaRepository<Permission, Integer> {

	void save(User optionalPermission);

	 @Query("select o from Permission o where o.role.id= :roleId")
    List<Permission> getAllUserPermisonsByRoleId(Integer roleId);


}
