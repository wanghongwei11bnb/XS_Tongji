package com.xiangshui.web.controller;

import com.xiangshui.util.web.result.CodeMsg;
import com.xiangshui.util.web.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Controller
public class XcxController extends BaseController {


    @GetMapping("/jpi/xcx/config")
    @ResponseBody
    public Result xcx_config() {

        Set<Integer> workAreaIds = new HashSet<>();
        workAreaIds.addAll(Arrays.asList(
                1100059,
                1100111,
                1100110,
                1100036,
                1100058,
                1100031,
                1100038,
                1100065,
                1100060,
                1100102,
                1100119,
                1100107,
                1100060,
                1100022,
                1100102,
                1100119,
                3100012,
                3100006,
                3100068,
                3100050,
                3100064,
                2601008,
                2601014,
                2601020,
                2601005,
                2601006,
                2601023,
                2601016,
                2501040,
                2501043,
                2501006,
                2501018,
                2501037,
                2501034,
                3403032,
                3403003,
                3403029,
                1100116,
                1100038,
                1100033,
                1100058,
                1100036,
                1100065,
                1100071,
                1100040,
                1100102,
                1100021,
                1100040,
                1100065,
                1100051,
                3100021,
                3100046,
                3100039,
                3100041,
                3100058,
                3100069,
                2601003,
                2601004,
                2601024,
                2601022,
                2501041,
                2501042,
                2501039,
                2501001,
                2501016,
                2501006,
                2501032,
                3403011,
                3403007,
                3403020,
                3403008,
                3403034,
                3403023,
                3403021,
                1100033,
                1100058,
                1100031,
                1100111,
                1100110,
                1100116,
                1100121,
                1100060,
                1100119,
                1100118,
                1100002,
                1100065,
                1100121,
                1100060,
                1100119,
                1100118,
                1100002,
                1100065,
                2601003,
                2601013,
                2601018,
                2601021,
                2601012,
                2601013,
                2601019,
                2501034,
                2501032,
                2501006,
                2501016,
                2501001,
                3100049,
                3100016,
                3100018,
                3100014,
                3100019,
                3100068,
                3403028,
                3403033,
                3403027,
                3403026,
                1100031,
                1100058,
                1100033,
                1100036,
                1100038,
                1100133,
                2702001,
                1100077,
                1100109,
                1100116,
                1100059,
                1100110,
                1100111,
                1100126,
                1100117,
                1100130,
                1100133,
                1100134,
                1100064,
                1100129,
                1100021,
                1100051,
                1100102,
                1100065,
                1100071,
                1100040,
                1100102,
                1100121,
                1100132,
                1100122,
                1100119,
                1100131,
                1100002,
                1100060,
                1100073,
                1100107,
                2601003,
                2601024,
                2601014,
                2601008,
                2601004,
                2601006,
                2601005,
                1500010,
                2601016,
                3403007,
                2601019,
                2601013,
                2601018,
                2601023,
                2601020,
                2501031,
                2501006,
                2501032,
                2501042,
                2501043,
                2501034,
                2501037,
                2501001,
                2501039,
                2501041,
                2501016,
                3100037,
                3100061,
                3100049,
                3100065,
                3100029,
                3100002,
                3100018,
                3100019,
                3100044,
                3100062,
                3100069,
                3100014,
                3100021,
                3100039,
                3100012,
                3100058,
                3100064,
                3100050,
                3100070,
                3403003,
                3403029,
                3403028,
                3403032,
                3403007,
                3403011,
                3403033,
                3403026,
                3403034,
                3403020,
                1100141,
                1100139,
                1100138,
                1100137,
                1100141,
                1200005,
                1200006,
                1100142,
                1100143,
                1100140,
                1100144


        ));

        return new Result(CodeMsg.SUCCESS)
                .putData("workAreaIds", workAreaIds)
                ;
    }


}
