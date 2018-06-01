package com.xiangshui.server.dao;

import com.xiangshui.server.domain.UserWallet;
import com.xiangshui.server.domain.WalletRecord;
import org.springframework.stereotype.Component;

@Component
public class WalletRecordDao extends BaseDynamoDao<WalletRecord> {

    @Override
    public String getTableName() {
        return "wallet_record";
    }


}
