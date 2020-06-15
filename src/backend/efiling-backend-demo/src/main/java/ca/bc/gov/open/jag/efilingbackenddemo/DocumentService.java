package ca.bc.gov.open.jag.efilingbackenddemo;

import ca.bc.gov.open.jag.efilingbackenddemo.api.DocumentApiDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DocumentService implements DocumentApiDelegate {

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public ResponseEntity<Resource> getDocumentById(String id) {
        return ResponseEntity.ok(resourceLoader.getResource("classpath:dummy.pdf"));
    }
}
