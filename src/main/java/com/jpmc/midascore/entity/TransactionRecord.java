package com.jpmc.midascore.entity;
import jakarta.persistence.*;

@Entity
public class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private float amount;

    @Column(nullable = false)
    private float incentive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    protected TransactionRecord() {
    }
    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
    }

    @Override
    public String toString(){
        return String.format("TransactionRecord[id=%d, sender='%s', recipient='%s', amount='%f']",
                            id, sender.getName(), recipient.getName(), amount);
    }

    public Long getId(){
        return id;
    }

    public UserRecord getSender(){
        return sender;
    }

    public UserRecord getRecipient(){
        return recipient;
    }

    public float getAmount(){
        return amount;
    }

    public float getIncentive() {
        return incentive;
    }
}

