import java.io.IOException;

/**
 * Created by Marek on 2017-01-19.
 */
public class StringDecoder {
    public String decodeMessage(String input)
    {
//        System.out.println(input);
        String output = null;
        String[] response = null;
//        System.out.println(output);
        String[] query = input.split("/");
        SNMPManager snmp = null;
        if(query.length == 7)
        {
            try {
                snmp = new SNMPManager(query[1], query[2], Integer.parseInt(query[3]), query[4]);

                if(query[0].equals("GET"))
                    response = snmp.snmpGet(query[5]);
                else if (query[0].equals("GETNEXT"))
                    response = snmp.snmpGetNext(query[5]);
                output = response[0] + "/" + response[1];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            output = null;

        return output;
    }
}
