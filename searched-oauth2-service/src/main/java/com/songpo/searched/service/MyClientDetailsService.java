package com.songpo.searched.service;

import com.songpo.searched.cache.UserCache;
import com.songpo.searched.entity.SlUser;
import com.songpo.searched.mapper.SlUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author SongpoLiu
 */
@Service
public class MyClientDetailsService implements ClientDetailsService {

    @Autowired
    private SlUserMapper mapper;

    @Autowired
    private UserCache cache;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        BaseClientDetails details = null;

        SlUser user = this.cache.get(clientId);
        if (null == user) {
            user = this.mapper.selectByPrimaryKey(clientId);
        }

        if (null != user) {
            details = new BaseClientDetails();
            details.setClientId(clientId);
            details.setClientSecret(user.getSecret());
            // 默认7200秒
            details.setAccessTokenValiditySeconds(7200);
            details.setAuthorizedGrantTypes(StringUtils.commaDelimitedListToSet("client_credentials"));
            details.setScope(StringUtils.commaDelimitedListToSet("read"));
        }
        return details;
    }
}