package com.xiangshui.op.scheduled;

import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.xiangshui.server.dao.AreaContractDao;
import com.xiangshui.server.dao.AreaDao;
import com.xiangshui.server.dao.BookingDao;
import com.xiangshui.server.dao.CapsuleDao;
import com.xiangshui.server.domain.Area;
import com.xiangshui.server.domain.AreaContract;
import com.xiangshui.server.domain.Capsule;
import com.xiangshui.util.MapOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheScheduled {


    @Autowired
    AreaDao areaDao;
    @Autowired
    CapsuleDao capsuleDao;
    @Autowired
    BookingDao bookingDao;
    @Autowired
    AreaContractDao areaContractDao;


    public MapOptions<Integer, Area> areaMapOptions;
    public MapOptions<Integer, AreaContract> areaContractMapOptions;
    public MapOptions<Long, Capsule> capsuleMapOptions;

    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void updateCache() {

        areaMapOptions = new MapOptions<Integer, Area>(areaDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE))) {
            @Override
            public Integer getPrimary(Area area) {
                return area.getArea_id();
            }
        };

        areaContractMapOptions = new MapOptions<Integer, AreaContract>(areaContractDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE))) {
            @Override
            public Integer getPrimary(AreaContract areaContract) {
                return areaContract.getArea_id();
            }
        };
        capsuleMapOptions = new MapOptions<Long, Capsule>(capsuleDao.scan(new ScanSpec().withMaxResultSize(Integer.MAX_VALUE))) {
            @Override
            public Long getPrimary(Capsule capsule) {
                return capsule.getCapsule_id();
            }
        };
    }


}
