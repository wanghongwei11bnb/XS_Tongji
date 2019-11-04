package com.xiangshui.op;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.TreeSet;

public class Test {

    public static void main(String[] args) throws Exception{
        Set<String> set=new TreeSet<>();
        for (String line : IOUtils.readLines(System.class.getResourceAsStream("/mnt_capsule_id.txt"))) {
            if(StringUtils.isNotBlank(line)){
                set.add(line);
            }
        }

        for (String s : set) {
            System.out.println(s);
        }


        for (String line : IOUtils.readLines(System.class.getResourceAsStream("/test.txt"))) {
            if(StringUtils.isNotBlank(line)){
                String[] ss=line.split("－");
                if(set.contains(ss[0])){
                    System.out.println("更换："+ss[0]+"＝》"+ss[1]);
                    set.remove(ss[0]);
                    set.add(ss[1]);
                }
            }
        }

        for (String s : set) {
            System.out.println(s);
        }



    }

}
