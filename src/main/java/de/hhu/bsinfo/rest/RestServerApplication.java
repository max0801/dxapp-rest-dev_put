package de.hhu.bsinfo.rest;



import com.google.gson.Gson;
import de.hhu.bsinfo.dxram.DXRAM;
import de.hhu.bsinfo.dxram.app.AbstractApplication;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.data.ChunkID;
import de.hhu.bsinfo.dxram.data.DataStructure;
import de.hhu.bsinfo.dxram.data.DummyDataStructure;
import de.hhu.bsinfo.dxram.engine.AbstractDXRAMService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.dxterm.TerminalServiceAccessor;
import de.hhu.bsinfo.dxutils.NodeID;
import de.hhu.bsinfo.rest.cmd.Chunkget;
import de.hhu.bsinfo.rest.cmd.Chunklist;
import de.hhu.bsinfo.rest.cmd.Nodelist;
import spark.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;


public class RestServerApplication extends AbstractApplication {

    private static Service server;
    private boolean run;
    private ChunkService chunkService;
    private BootService bootService;
    private Gson gson;

    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return DXRAM.VERSION;
    }

    @Override
    public String getApplicationName() {
        return "RestServer";
    }

    @Override
    public boolean useConfigurationFile() {
        return false;
    }

    @Override
    public void main() {
        ServiceHelper services = new ServiceHelper(super.getService(BootService.class), super.getService(NameserviceService.class),super.getService(ChunkService.class));

        gson = new Gson();
        run = true;

        System.out.println("Starting REST Server ............. :)");
        startServer();
        String[] commands = {"nodelist, chunklist, chunkget, abstract"};

        server.get("/", (req, res) -> gson.toJson(commands));

        List<AbstractRestCommand> restCommands = new ArrayList<>();
        restCommands.add(new Chunkget());
        restCommands.add(new Chunklist());
        restCommands.add(new Nodelist());

        for (AbstractRestCommand c : restCommands) {
            c.register(server, services);
        }

        while(run){
            //keep server alive
        }
    }


    private static void startServer(){
        server = Service.ignite().port(8009);


    }

    @Override
    public void signalShutdown() {
        server.stop();
        run = false;

    }

}
