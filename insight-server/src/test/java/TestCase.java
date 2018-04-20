import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author liuzhihao
 * @date 2018/4/12
 */
@Slf4j
@Builder
@Data
public class TestCase {

    private Integer x;

    private Integer y;

    private Integer z;

    public static void main(String[] args) {
        TestCase build = TestCase.builder().x(1).build();
        List<TestCase> data = Lists.newArrayList(build);

        System.out.println(data.stream().reduce((x, y) -> TestCase.builder().x(x.getX() + y.getX()).build()).get());
    }
}
