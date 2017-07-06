/**
 * Created by ccb on 2017/7/3.
 */

import com.ccb.job.admin.Application;
import com.ccb.job.admin.core.util.SmsUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SmsTest {

    @Test
     public void testSms(){
        SmsUtil.sendSingleMsg("15221179520","ceshi");
     }

}
