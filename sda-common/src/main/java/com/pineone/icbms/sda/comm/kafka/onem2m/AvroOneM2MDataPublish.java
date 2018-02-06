package com.pineone.icbms.sda.comm.kafka.onem2m;

import java.io.IOException;
import java.util.Properties;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

import com.pineone.icbms.sda.comm.kafka.avro.COL_ONEM2M;
import com.pineone.icbms.sda.comm.util.Utils;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * OneM2M data를 produce
 */
public class AvroOneM2MDataPublish { 
	private  Producer<String, byte[]> producer;
	
	private final String TOPIC = Utils.KafkaTopics.COL_ONEM2M.toString();

	public AvroOneM2MDataPublish(String broker) {
		Properties props = new Properties();
		props.put("metadata.broker.list", Utils.BROKER_LIST);
		props.put("serializer.class", "kafka.serializer.DefaultEncoder");
		props.put("partitioner.class", "kafka.producer.DefaultPartitioner");
		props.put("request.required.acks", "1");
		producer = new Producer<String, byte[]>(new ProducerConfig(props));

	}
	
	/**
	 * 데이타 전송
	 * @param event
	 * @throws Exception
	 * @return void
	 */
	public void send(COL_ONEM2M event) throws Exception {
		EncoderFactory avroEncoderFactory = EncoderFactory.get();
		SpecificDatumWriter<COL_ONEM2M> avroEventWriter = new SpecificDatumWriter<COL_ONEM2M>(COL_ONEM2M.SCHEMA$);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		BinaryEncoder binaryEncoder = avroEncoderFactory.binaryEncoder(stream,null);

		try {
			avroEventWriter.write(event, binaryEncoder);
			binaryEncoder.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		IOUtils.closeQuietly(stream);

		KeyedMessage<String, byte[]> data = new KeyedMessage<String, byte[]>(
				TOPIC, stream.toByteArray());

		producer.send(data);
	}
	
	/**
	 * 커넥션 닫기
	 * @return void
	 */
	public void close() {
		producer.close();
	}
	
}