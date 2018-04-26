package com.xiangshui.server.dao;

import com.xiangshui.server.domain.UserWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserWalletDao extends BaseDynamoDao<UserWallet> {




    @Override
    public String getTableName() {
        return  "user_wallet";
    }


}
