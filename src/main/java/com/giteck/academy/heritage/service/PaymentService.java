package com.giteck.academy.heritage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;

@Service
public class PaymentService {

    // On déclare explicitement le rollback pour l'exception checked IOException
    @Transactional(rollbackFor = IOException.class)
    public void exportPayments(String filePath) throws IOException {
        // Logique fictive d'export
        boolean errorOccurred = true;

        if (errorOccurred) {
            // Si cette exception remonte, la transaction sera annulée (rollback)
            throw new IOException("Erreur lors de l'écriture du fichier d'export");
        }
    }
}