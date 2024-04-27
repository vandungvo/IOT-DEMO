package bku.iot.demoiot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;
    TextView txtTemperature, txtHumidity;
    LabeledSwitch btnLED, btnPUMP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTemperature = findViewById(R.id.txtTemparature);
        txtHumidity = findViewById(R.id.txtHumidity);

        btnLED = findViewById(R.id.btnLED);
        btnPUMP = findViewById(R.id.btnPUMP);

        btnLED.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn) {
                    sendDataMQTT("vovandung/feeds/nutnhan1", "1");
                } else {
                    sendDataMQTT("vovandung/feeds/nutnhan1", "0");
                }
            }
        });
        btnPUMP.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn) {
                    sendDataMQTT("vovandung/feeds/nutnhan2", "1");
                } else {
                    sendDataMQTT("vovandung/feeds/nutnhan2", "0");
                }
            }
        });

        startMQTT();
    }

    public void sendDataMQTT(String topic, String value) {
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        } catch (MqttException e){
        }
    }

    public void startMQTT() {
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("Test", topic + "***" + message.toString());
                if (topic.contains("cambien1")) {
                    String temperature = message.toString() + "°C";
                    txtTemperature.setText(temperature);
                } else if (topic.contains("cambien2")) {
                    String humidity = message.toString() + "%";
                    txtHumidity.setText(humidity);
                } else if (topic.contains("nutnhan1")) {
                    if (message.toString().equals("1")) {
                        btnLED.setOn(true);
                    } else {
                        btnLED.setOn(false);
                    }
                } else if (topic.contains("nutnhan2")) {
                    if (message.toString().equals("1")) {
                        btnPUMP.setOn(true);
                    } else {
                        btnPUMP.setOn(false);
                    }
                } else {
                    throw new Exception("Message is not from chosen feeds");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}