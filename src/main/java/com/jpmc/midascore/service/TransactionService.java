package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jpmc.midascore.service.IncentiveService;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private IncentiveService incentiveService;

    @Transactional
    public boolean processTransaction(Transaction transaction){
        logger.info("Processing transaction: {}", transaction);

        //validate sender exists
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        if(sender == null){
            logger.warn("Transaction rejected: sender with ID {} does not exist", transaction.getSenderId());
            return false;
        }

        //validate recipient exists
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());
        if(recipient == null){
            logger.warn("Transaction rejected: recipient with ID {} does not exist", transaction.getRecipientId());
            return false;
        }

        //validate sender has enough balance
        if(sender.getBalance() < transaction.getAmount()){
            logger.warn("Transaction rejected: sender with ID {} has insufficient balance. Required: {}, Available: {}", transaction.getSenderId(), transaction.getAmount(), sender.getBalance());
            return false;
        }
        
        //process the transaction
        try{
            //call incentive API
            Incentive incentive = incentiveService.getIncentive(transaction);

            //update balances
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentive.getAmount());

            //save updated balances
            userRepository.save(sender);
            userRepository.save(recipient);

            //create transaction record
            TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount(), incentive.getAmount());
            transactionRecordRepository.save(transactionRecord);

            logger.info("Transaction processed successfully: {} -> {} (amount: {})", 
                sender.getName(), recipient.getName(), transaction.getAmount());
            return true;

        } catch (Exception e){
            logger.error("Transaction processing failed: {}", e.getMessage(), e);
            return false;
        }
    }
}