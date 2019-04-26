package rocketmq_example.quickstart;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class ProducerTest {
	public static void main(String[] args) throws MQClientException, InterruptedException {

		/*
		 * Instantiate with a producer group name.
		 */
		DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");

		producer.setNamesrvAddr("10.10.6.71:9876;10.10.6.72:9876");
		/*
		 * Specify name server addresses. <p/>
		 *
		 * Alternatively, you may specify name server addresses via exporting
		 * environmental variable: NAMESRV_ADDR <pre> {@code
		 * producer.setNamesrvAddr("name-server1-ip:9876;name-server2-ip:9876");
		 * } </pre>
		 */

		/*
		 * Launch the instance.
		 */
		producer.start();

		ExecutorService executorService = new ThreadPoolExecutor(50, 50, 100, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r);
						thread.setName("test-send-mq");
						return thread;
					}
				});

		for (int i = 0; i < 1000; i++) {

			executorService.execute(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < 100000; i++) {

						Message msg;
						try {
							msg = new Message("TopicTest", "TagA",
									(i + "-----Hello RocketMQ aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaassssssssssssssssssssssssddddddddddddddddddddfffffffffffffffffsddssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss")
											.getBytes(RemotingHelper.DEFAULT_CHARSET));
							producer.send(msg);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

			});

		}

		/*
		 * Shut down once the producer instance is not longer in use.
		 */
		// producer.shutdown();
	}
}
