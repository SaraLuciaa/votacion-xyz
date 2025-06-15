module VotacionXYZ {
    struct Voto {
        string nombreCandidato;
    };

    struct Message {
        string id;
        Voto voto;
    };

    struct Candidato {
        int id;
        string nombre;
        string apellido;
        string nombrePartido;
    };

    struct Ciudadano {
        string documento;
    };

    sequence<Ciudadano> CiudadanoSeq;
    sequence<Candidato> CandidatoSeq;

    struct DatosMesa {
        CiudadanoSeq ciudadanos;
        CandidatoSeq candidatos;
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
        DatosMesa sendData(string mesaId);
    }

    interface VoteStation{
        int vote(string document, int candidateId);
    }
};
