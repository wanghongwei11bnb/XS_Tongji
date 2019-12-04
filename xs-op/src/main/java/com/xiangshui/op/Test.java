package com.xiangshui.op;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.TreeSet;

@Slf4j
public class Test {

    public static void main(String[] args) throws Exception {
        Set<String> set = new TreeSet<>();
        for (String line : IOUtils.readLines(System.class.getResourceAsStream("/mnt_capsule_id.txt"))) {
            if (StringUtils.isNotBlank(line)) {
                set.add(line);
            }
        }

        log.info("替换前数量{}", set.size());

        for (String line : IOUtils.readLines(System.class.getResourceAsStream("/mnt_capsule_id_change.txt"))) {
            if (StringUtils.isBlank(line)) continue;
            String[] ss = line.split("-");
            if (!set.contains(ss[0])) {
                log.info("头等舱编号 {} 不存在", ss[0]);
                continue;
            }
            if(set.contains(ss[1])){
                log.info("头等舱编号 {} 已存在", ss[1]);
                continue;
            }
            log.info("更换：{}＝》{}", ss[0],ss[1]);
            set.remove(ss[0]);
            set.add(ss[1]);
        }


        log.info("替换后数量{}", set.size());
        StringBuilder sb = new StringBuilder().append("\n");
        for (String s : set) {
            sb.append(s).append("\n");
        }
        log.info(sb.toString());
    }

}
