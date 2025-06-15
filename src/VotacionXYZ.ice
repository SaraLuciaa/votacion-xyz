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
        void send(Message msg);
        void setServerProxy(RmReceiver* receiver);
    }

    interface queryStation{
        string query(string document);
    }

    interface DataDistribution{
        void sendData(string mesaId);
    }

    interface VoteStation{
        int vote(string document, int candidateId);
    }
};
