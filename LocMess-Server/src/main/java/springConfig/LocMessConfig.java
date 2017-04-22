package springConfig;

/**
 * Created by lads on 22/04/2017.
 */
import java.util.List;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Configuration
@ComponentScan(basePackages = {"ist.meic.cmu.locmess.api.controller"})
@EnableWebMvc
public class LocMessConfig extends WebMvcConfigurerAdapter  {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        GsonHttpMessageConverter msgConverter = new GsonHttpMessageConverter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        msgConverter.setGson(gson);
        converters.add(msgConverter);
    }
}
