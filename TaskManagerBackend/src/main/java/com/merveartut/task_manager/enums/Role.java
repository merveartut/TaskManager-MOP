package com.merveartut.task_manager.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    PROJECT_MANAGER,
    TEAM_LEADER,
    TEAM_MEMBER,
    GUEST;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
