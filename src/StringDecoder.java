import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

                if(query[5].contains("1.3.6.1.2.1.2.2"))
                    response = createTable(query[5], snmp);
                else if(query[0].equals("GET"))
                    response = snmp.snmpGet(query[5]);
                else if(query[5].contains("1.3.6.1.2.1.2.1.0"))
                    response = createTable("1.3.6.1.2.1.2.2.0", snmp);
                else if (query[0].equals("GETNEXT"))
                    response = snmp.snmpGetNext(query[5]);
                output = response[0] + "%" + response[1];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            output = null;

        return output;
    }

    public String[] createTable(String oid, SNMPManager snmp) throws IOException {
        String rowNumber = null;
        if(oid.length() == "1.3.6.1.2.1.2.2.0".length())
            rowNumber = "";
        else
            rowNumber = oid.substring("1.3.6.1.2.1.2.2.".length(), oid.length() - 2);
        System.out.println("===> " + rowNumber);
        String[] numbers = rowNumber.split(".");
        String output = "";
        String[] response;
        if(rowNumber.length() == "1.1.2".length() || rowNumber.length() == "1.1.20".length())
        {
            //Wiersz
            System.out.println("nub == 3");
            int row = Integer.parseInt(rowNumber.substring(rowNumber.lastIndexOf('.') + 1, rowNumber.length()));
            System.out.println(row);
            TableModel table = snmp.getTable("1.3.6.1.2.1.2.2");
            List<String> col = table.getColumns();
            List<List<String>> rows = table.getRows();

            if(row < rows.get(0).size()) {
                for (int i = 0; i < col.size(); i++) {
                    output += getIFColumn(i) + ":\t " + rows.get(i).get(row) + "\n";
                }
                response = new String[2];
                response[0] = "1.3.6.1.2.1.2.2.1.1." + rows.get(0).get(row);
                response[1] = output;
            } else
            {
                response = snmp.snmpGetNext("1.3.6.1.2.1.2.2.1.22.42");
            }
        } else
        {
            int row = 0;
            TableModel table = snmp.getTable("1.3.6.1.2.1.2.2");
            List<String> col = table.getColumns();
            List<List<String>> rows = table.getRows();
            for (int i = 0; i < col.size(); i++) {
                output += getIFColumn(i) + ":\t " + rows.get(i).get(row) + "\n";
            }
            response = new String[2];
            response[0] = "1.3.6.1.2.1.2.2.1.1.1";
            response[1] = output;
        }
        System.out.println(response[0].length() + response[1].length());
        return response;
    }

    private String getIFColumn(int i)
    {
        List<String> listOfColumns = new ArrayList<>();
        listOfColumns.add("ifIndex");
        listOfColumns.add("ifDescr");
        listOfColumns.add("ifType");
        listOfColumns.add("ifMtu");
        listOfColumns.add("ifSpeed");
        listOfColumns.add("ifPhysAddress");
        listOfColumns.add("ifAdminStatus");
        listOfColumns.add("ifOperStatus");
        listOfColumns.add("ifLastChange");
        listOfColumns.add("ifInOctets");
        listOfColumns.add("ifInUcastPkts");
        listOfColumns.add("ifInNUcastPkts");
        listOfColumns.add("ifInDiscards");
        listOfColumns.add("ifInErrors");
        listOfColumns.add("ifInUnknownProtos");
        listOfColumns.add("ifOutOctets");
        listOfColumns.add("ifOutUcastPkts");
        listOfColumns.add("ifOutNUcastPkts");
        listOfColumns.add("ifOutDiscards");
        listOfColumns.add("ifOutErrors");
        listOfColumns.add("ifOutQLen");
        listOfColumns.add("ifSpecific");

        return listOfColumns.get(i);
    }
}
