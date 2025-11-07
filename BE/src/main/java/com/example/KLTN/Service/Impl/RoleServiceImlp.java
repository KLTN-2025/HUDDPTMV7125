package com.example.KLTN.Service.Impl;

import com.example.KLTN.Entity.RoleEntity;
import com.example.KLTN.Entity.UsersEntity;
public interface RoleServiceImlp {
    void SaveRole(RoleEntity role);
    RoleEntity finByRolename(String rolename);
    Boolean existByRolename(String rolename);


}
