module VotacionXYZ {
    sequence<string> stringSeq;

    interface MesaVotacion {
        stringSeq listarCandidatos();
        void registrarVoto(long candidatoId);
        stringSeq obtenerResultados();
    };

     interface AckService {
        void confirm(string messageId);
    }

    interface EstacionVotacion{
        stringSeq obtenerAcumuladoVotos();
        void obtenerVoto();
    }
};
