package com.cf.springbootbackendapirest.models.services;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface UploadFileService {
    Resource cargar(String nombreFoto) throws MalformedURLException;
    String copiar(MultipartFile archivo) throws IOException;
    boolean eliminar(String nombreFoto);
    Path getPath(String nombreFoto);
}
