package com.mgtu.akashkin;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mgtu.akashkin.model.HashInfo;
import com.mgtu.akashkin.service.HashService;
import com.mgtu.akashkin.service.HashServiceImpl;
import com.pragone.jphash.image.radial.RadialHash;
import com.pragone.jphash.jpHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.Utils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootApplication
public class Application {
    static final String URL_API_USERS = "http://localhost:8087/api-users/";

    static final String URL_API_USERS_GET = URL_API_USERS.concat("getAll");
    static final String URL_API_USERS_REGISTR = URL_API_USERS.concat("create");
    @Autowired
    private static HashService hashService;
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(Application.class, args);

            long startTime = System.currentTimeMillis();
            HashInfo hash = new HashInfo();
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL_API_USERS_GET);


            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(builder.toUriString(), String.class);


             System.out.println("hashes:" + result);
            String filename = "C:/test/video/videoplayback.mp4";
            File outdir = new File("c:/test/picture");
            IContainer container = IContainer.make();

            if (container.open(filename, IContainer.Type.READ, null) < 0)
                throw new IllegalArgumentException("could not open file: "
                        + filename);
            int numStreams = container.getNumStreams();
            int videoStreamId = -1;
            IStreamCoder videoCoder = null;

            // нужно найти видео поток
            for (int i = 0; i < numStreams; i++) {
                IStream stream = container.getStream(i);
                IStreamCoder coder = stream.getStreamCoder();
                if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                    videoStreamId = i;
                    videoCoder = coder;
                    break;
                }
            }
            if (videoStreamId == -1)
                // кажись не нашли
                throw new RuntimeException("could not find video stream in container: "
                        + filename);

            // пытаемся открыть кодек
            if (videoCoder.open() < 0)
                throw new RuntimeException(
                        "could not open video decoder for container: " + filename);

            IPacket packet = IPacket.make();
            // с 3-ей по 5-ую микросекунду
            long start = 0;
            long end = Long.MAX_VALUE;
            // с разницей в 100 милисекунд
            long step = 500 * 1000;

            END:
            while (container.readNextPacket(packet) >= 0) {
                if (packet.getStreamIndex() == videoStreamId) {
                    IVideoPicture picture = IVideoPicture.make(
                            videoCoder.getPixelType(), videoCoder.getWidth(),
                            videoCoder.getHeight());
                    int offset = 0;
                    while (offset < packet.getSize()) {
                        int bytesDecoded = videoCoder.decodeVideo(picture, packet,
                                offset);
                        // Если что-то пошло не так
                        if (bytesDecoded < 0)
                            throw new RuntimeException("got error decoding video in: "
                                    + filename);
                        offset += bytesDecoded;
                        // В общем случае, нужно будет использовать Resampler. См.
                        if (picture.isComplete()) {
                            IVideoPicture newPic = picture;
                            // в микросекундах
                            long timestamp = picture.getTimeStamp();
                            if (timestamp > start) {
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                // Получаем стандартный BufferedImage
                                BufferedImage javaImage = Utils
                                        .videoPictureToImage(newPic);

                                RadialHash hash1 = jpHash.getImageRadialHash(javaImage);
                                hash.setHash(hash1.toString());


                                RestTemplate restTemplate2 = new RestTemplate();


                                HttpHeaders headers2 = new HttpHeaders();
                                headers2.setContentType(MediaType.APPLICATION_JSON);
                                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

                                String requestJson =  ow.writeValueAsString(hash);;
                                HttpEntity<String> entity2 = new HttpEntity<String>(requestJson,headers2);
                                restTemplate2.postForObject(URL_API_USERS_REGISTR, entity2, String.class);
//                               hashService.registrationUser(hash);

                                // System.out.println("Hash1: " + hash1);
                                //   RadialHash hash2 = jpHash.getImageRadialHash("c:/test/picture/0500500.png");
                                //  System.out.println("Hash2: " + hash2);

                                //    System.out.println("Similarity: " + jpHash.getSimilarity(hash1, hash2));
                                start += step;
                            }
                            if (timestamp > end) {
                                break END;
                            }
                        }
                    }
                }
            }
            if (videoCoder != null) {
                videoCoder.close();
                videoCoder = null;
            }
            if (container != null) {
                container.close();
                container = null;
            }

            long timeSpent = System.currentTimeMillis() - startTime;
            System.out.println("программа выполнялась " + timeSpent + " миллисекунд");

        } catch (Exception e) {
            logger.error("getAllError", e);

        }


    }
}
