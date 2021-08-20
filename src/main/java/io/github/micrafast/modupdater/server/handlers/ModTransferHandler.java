package io.github.micrafast.modupdater.server.handlers;

import io.github.micrafast.modupdater.Mod;
import io.github.micrafast.modupdater.server.ModManifestManager;
import io.github.micrafast.modupdater.server.ServerConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

public class ModTransferHandler implements HttpRequestHandler {
    protected final Log log = LogFactory.getLog(getClass());
    ServerConfig config;
    ModManifestManager manifestManager;

    public ModTransferHandler(ServerConfig config, ModManifestManager manifestManager) {
        this.config = config;
        this.manifestManager = manifestManager;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        String[] parts = request.getRequestLine().getUri().split("/");
        String md5 = parts[parts.length-1];
        Mod mod = manifestManager.getModManifest().searchMD5(md5);
        File modFile;
        if (mod != null) {
            modFile = mod.localFile;
        } else {
            String fileName = URLDecoder.decode(parts[parts.length-1], "UTF-8");
            modFile = new File(this.config.commonModsFolder, fileName);
            if (!modFile.exists()) {
                modFile = new File(this.config.optionalModsFolder, fileName);
            }
        }
        if (!modFile.exists()) {
            response.setStatusCode(404);
            return;
        }
        response.setStatusCode(200);
        response.setEntity(new FileEntity(modFile));
    }
}
