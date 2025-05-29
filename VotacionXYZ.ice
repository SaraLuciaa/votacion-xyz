module VotacionXYZ {
    sequence<string> stringSeq;

    struct Voto {
        string nombreCandidato;
    };

    struct Message {
        string id;
        Voto voto;
    };

    interface MesaVotacion {
        stringSeq listarCandidatos();
        void registrarVoto(long candidatoId);
        stringSeq obtenerResultados();
    }

     interface AckService {
        void confirm(string messageId);
    }

    interface EstacionVotacion{
        stringSeq obtenerAcumuladoVotos();
        void obtenerVoto(Message m, AckService* ack);
    }
};
