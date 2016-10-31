package zipkin.brave;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Controller
@RequestMapping("/")
public class HomeController {
	private static Logger log = LoggerFactory.getLogger(HomeController.class);

	@Value("${com.zipkin.serviceName}")
	private String serviceName;

	@Autowired
	private OkHttpClient client;

	private Random random = new Random();

	private int sleep(int min, int max) throws InterruptedException {
		int sleep = random.nextInt(max - min) + min;
		TimeUnit.MILLISECONDS.sleep(sleep);
		return sleep;
	}

	@RequestMapping("start")
	public @ResponseBody String start() throws InterruptedException, IOException {
		log.info("begin:-------------------");
		int sleep = sleep(10, 100);
		Request request = new Request.Builder().url("http://localhost:9092/foo").get().build();
		Response response = client.newCall(request).execute();
		String result = " [" + serviceName + " start sleep " + sleep + " ms]" + response.body().string();
		log.info("end:\n{}", result);
		return result;
	}

	@RequestMapping("foo")
	public @ResponseBody String foo() throws InterruptedException, IOException {
		log.info("begin:-------------------");
		int sleep = sleep(10, 200);
		Request request = new Request.Builder().url("http://localhost:9093/bar").get().build(); // service3
		Response response = client.newCall(request).execute();
		String result = response.body().string();
		request = new Request.Builder().url("http://localhost:9094/tar").get().build(); // service4
		response = client.newCall(request).execute();
		result += response.body().string();
		result = " [" + serviceName + " foo sleep " + sleep + " ms]" + result;
		log.info("end:\n{}", result);
		return result;
	}

	/**
	 * service3 method
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@RequestMapping("bar")
	public @ResponseBody String bar() throws InterruptedException, IOException {
		log.info("begin:-------------------");
		int sleep = sleep(200, 400);
		TimeUnit.MILLISECONDS.sleep(sleep);
		String result = " [" + serviceName + " bar sleep " + sleep + " ms]";
		log.info("end:\n{}", result);
		return result;
	}

	/**
	 * service4 method
	 * 
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@RequestMapping("tar")
	public @ResponseBody String tar() throws InterruptedException, IOException {
		log.info("begin:-------------------");
		int sleep = sleep(500, 2000);
		TimeUnit.MILLISECONDS.sleep(sleep);
		String result = " [" + serviceName + " tar sleep " + sleep + " ms]";
		log.info("end:\n{}", result);
		return result;
	}

}
