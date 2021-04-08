package com.xiangshui.web.controller;

import com.alibaba.fastjson.JSON;
import com.xiangshui.server.dao.*;
import com.xiangshui.server.dao.mysql.PrizeQuotaDao;
import com.xiangshui.server.dao.mysql.SwiperItemDao;
import com.xiangshui.server.dao.mysql.TransactionDao;
import com.xiangshui.util.DateUtils;
import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import com.xiangshui.web.scheduled.ArticleScheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class BaseController {


    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ArticleScheduled articleScheduled;

    @Autowired
    UserInfoDao userInfoDao;

    @Autowired
    UserRegisterDao userRegisterDao;

    @Autowired
    UserWalletDao userWalletDao;
    @Autowired
    WalletRecordDao walletRecordDao;

    @Autowired
    AreaDao areaDao;

    @Autowired
    CapsuleDao capsuleDao;

    @Autowired
    CityDao cityDao;
    @Autowired
    SwiperItemDao swiperItemDao;

    @Autowired
    BookingDao bookingDao;


    @Autowired
    PrizeQuotaDao prizeQuotaDao;



    @Autowired
    TransactionDao transactionDao;



    @Value("${isdebug}")
    boolean debug;

    private long ts = System.currentTimeMillis();

    public void setClient(HttpServletRequest request) {
        request.setAttribute("debug", debug);
        request.setAttribute("isPhone", isPhone(request));
        request.setAttribute("countArticle", articleScheduled.countArticle);
        request.setAttribute("ts", ts + "_" + (debug ? System.currentTimeMillis() : DateUtils.format("yyyyMMddHH")));
        request.setAttribute("DateUtils", DateUtils.class);
        request.setAttribute("JSON", JSON.class);
        request.setAttribute("Math", Math.class);
    }

    /**
     * 基于@ExceptionHandler异常处理
     */
    @ExceptionHandler
    @ResponseBody
    public Result exp(HttpServletRequest request, Exception e) {
        log.error("", e);
        return new Result(CodeMsg.SERVER_ERROR.code, e.getMessage());
    }


    static Pattern phonePat = Pattern.compile("\\bNokia|SAMSUNG|MIDP-2|CLDC1.1|SymbianOS|MAUI|UNTRUSTED/1.0"
            + "|Windows CE|iPhone|iPad|Android|BlackBerry|UCWEB|ucweb|BREW|J2ME|YULONG|YuLong|COOLPAD|TIANYU|TY-"
            + "|K-Touch|Haier|DOPOD|Lenovo|LENOVO|HUAQIN|AIGO-|CTC/1.0"
            + "|CTC/2.0|CMCC|DAXIAN|MOT-|SonyEricsson|GIONEE|HTC|ZTE|HUAWEI|webOS|GoBrowser|IEMobile|WAP2.0\\b", Pattern.CASE_INSENSITIVE);

    public static boolean isPhone(HttpServletRequest request) {
        String userAgent = request.getHeader("USER-AGENT");
        Matcher matcherPhone = phonePat.matcher(userAgent);
        if (matcherPhone.find()) {
            return true;
        } else {
            return false;
        }
    }

}
