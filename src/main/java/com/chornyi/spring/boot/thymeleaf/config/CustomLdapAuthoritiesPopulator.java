package com.chornyi.spring.boot.thymeleaf.config;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class CustomLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private static final String REGEX = ";";
    
    private final LdapTemplate ldapTemplate;
    private final String groupSearchBase;

    public CustomLdapAuthoritiesPopulator(ContextSource contextSource, String groupSearchBase) {
        this.ldapTemplate = new LdapTemplate(contextSource);
        this.groupSearchBase = groupSearchBase;
    }

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(
            DirContextOperations userData, String username) {

        log.debug("Getting authorities for user " + userData.getNameInNamespace());

        val authorities = new HashSet<GrantedAuthority>();

        val groups = getAllGroups();
        for (String group: groups) {
            val attr = group.split(REGEX);
            if (userData.getNameInNamespace().equalsIgnoreCase(attr[1])) {
                authorities.add(new SimpleGrantedAuthority(attr[0]));
            }
        }

        return authorities;
    }

    private List<String> getAllGroups() {
        AttributesMapper<String> mapper = attrs -> attrs.get("cn").get() + REGEX + attrs.get("uniqueMember").get();
        return ldapTemplate.search(groupSearchBase, "(objectClass=groupOfUniqueNames)", mapper);
    }

}
