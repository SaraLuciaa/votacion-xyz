module VotacionXYZ {
    struct Voto {
        int idCandidato;
        int mesaId;  
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
        int mesaId;
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

    interface VoteStation {
        CandidatoSeq obtenerCandidatos();
        int consultarCiudadanoPorId(string documento, int mesaId);
        void registrarVoto(int candidato, string documento,int mesaId);
    }
};
