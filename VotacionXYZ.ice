module VotacionXYZ {
    struct Voto {
        string nombreCandidato;
    };

    struct Message {
        string id;
        Voto voto;
    };

    interface AckService {
        void confirm(string messageId);
    }

    interface RmReceiver {
        void receiveMessage(Message m, AckService* ack);
    }

    interface RmSender {
        void send(Message msg, AckService* ack);
        void setServerProxy(RmReceiver* receiver);
    };

};
