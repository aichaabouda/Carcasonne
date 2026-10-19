package ch.epfl.chacun;
/** ZONEPARTITIONS
 * @author Aicha Abouda (361079)
 * @author Soundouss Idrissi Kassimy (359384)
 */

/**L'enregistrement ZonePartitions
 * regroupe les quatre partitions de zones du jeu*/
public record ZonePartitions(ZonePartition<Zone.Forest> forests, ZonePartition<Zone.Meadow> meadows,
                             ZonePartition<Zone.River> rivers, ZonePartition<Zone.Water> riverSystems) {

    /** représente un groupe de 4 partitions vides*/
    public static final ZonePartitions EMPTY = new ZonePartitions(new ZonePartition<>(), new ZonePartition<>(),
            new ZonePartition<>(), new ZonePartition<>());
    /**
     * Classe Builder pour la construction d'objets ZonePartitions.
     * Cette classe est responsable de la construction d'objets ZonePartitions en ajoutant des tuiles,
     * des occupants initiaux, en connectant les côtés et en effectuant d'autres opérations nécessaires.
     */

    public final static class Builder {
        private final ZonePartition.Builder<Zone.Forest> forestBuilder;
        private final ZonePartition.Builder<Zone.Meadow> meadowBuilder;
        private final ZonePartition.Builder<Zone.River> riverBuilder;
        private final ZonePartition.Builder<Zone.Water> waterBuilder;

        /**
         * Construit une nouvelle instance de Builder.
         *
         * @param initial L'objet ZonePartitions initial avec lequel commencer.
         */

        public Builder(ZonePartitions initial) {
            this.forestBuilder = new ZonePartition.Builder<>(initial.forests);
            this.meadowBuilder = new ZonePartition.Builder<>(initial.meadows);
            this.riverBuilder = new ZonePartition.Builder<>(initial.rivers);
            this.waterBuilder = new ZonePartition.Builder<>(initial.riverSystems);

        }

        /**
         * Ajoute les aires correspondant aux zones de la tuile donnée aux partitions respectives.
         *
         * @param tile La tuile à ajouter.
         */
        public void addTile(Tile tile) {
            int[] openConnections = new int[10];
            for (TileSide side : tile.sides()) {
                for (Zone zone : side.zones()) {
                    openConnections[zone.localId()]+=1;
                    if (zone instanceof Zone.River river && river.hasLake()) {
                        openConnections[river.localId()]+=1;
                        openConnections[river.lake().localId()]+=1;
                    }
                }
            }
            for (Zone zone : tile.zones()) {
                int nbOpenConnections=openConnections[zone.localId()];
                switch (zone) {
                    case Zone.Meadow meadow-> meadowBuilder.addSingleton(meadow, nbOpenConnections);
                    case Zone.Forest forest -> forestBuilder.addSingleton(forest, nbOpenConnections);
                    case Zone.River river ->
                    {
                        waterBuilder.addSingleton(river, nbOpenConnections);
                        if(river.hasLake()){
                            riverBuilder.addSingleton(river, nbOpenConnections - 1);
                        }else{
                            riverBuilder.addSingleton(river, nbOpenConnections);
                        }

                    }
                    case Zone.Lake lake -> waterBuilder.addSingleton(lake, nbOpenConnections);

                }

            }
            for(Zone zone: tile.sideZones()){
                if(zone instanceof Zone.River river && river.hasLake())
                {
                    waterBuilder.union(river, river.lake());
                }

            }

        }

        /**
         * Ajoute un occupant initial à une zone spécifique dans les ZonePartitions en cours de construction.
         *
         * @param player        La couleur du joueur de l'occupant.
         * @param occupantKind  Le type d'occupant à ajouter.
         * @param occupiedZone  La zone où l'occupant est initialement placé.
         * @throws IllegalArgumentException Si le type d'occupant ne peut pas occuper la zone donnée.
         */

        public void addInitialOccupant(PlayerColor player, Occupant.Kind occupantKind, Zone occupiedZone) {
            switch (occupiedZone) {
                case Zone.Meadow meadow when occupantKind== Occupant.Kind.PAWN ->
                        meadowBuilder.addInitialOccupant(meadow, player);
                case Zone.Forest forest when occupantKind== Occupant.Kind.PAWN ->
                        forestBuilder.addInitialOccupant(forest, player);
                case Zone.River river when occupantKind== Occupant.Kind.PAWN ->
                        riverBuilder.addInitialOccupant(river, player);
                case Zone.Water water when occupantKind== Occupant.Kind.HUT ->
                        waterBuilder.addInitialOccupant(water, player);
                default -> throw new IllegalArgumentException();
            }
        }

        /**
         * Supprime un pion d'une zone spécifique dans les ZonePartitions en cours de construction.
         *
         * @param player        La couleur du joueur du pion à supprimer.
         * @param occupiedZone  La zone à partir de laquelle supprimer le pion.
         * @throws IllegalArgumentException Si la zone est un lac.
         */
        public void removePawn(PlayerColor player, Zone occupiedZone) {
            switch (occupiedZone) {
                case Zone.Meadow meadow -> meadowBuilder.removeOccupant(meadow, player);
                case Zone.Forest forest -> forestBuilder.removeOccupant(forest, player);
                case Zone.River river -> riverBuilder.removeOccupant(river, player);
                default -> throw new IllegalArgumentException();
            }

        }

        /**
         * Connecte deux côtés de tuiles dans les ZonePartitions en cours de construction.
         *
         * @param s1 Le premier côté à connecter.
         * @param s2 Le second côté à connecter.
         * @throws IllegalArgumentException Si les deux côtés ne sont pas de la même sorte.
         */
        public void connectSides(TileSide s1, TileSide s2) {
            switch (s1) {
                case TileSide.Meadow(Zone.Meadow m1)
                        when s2 instanceof TileSide.Meadow(Zone.Meadow m2) -> meadowBuilder.union(m1, m2);
                case TileSide.Forest(Zone.Forest m3)
                        when s2 instanceof TileSide.Forest(Zone.Forest m4) -> forestBuilder.union(m3, m4);
                case TileSide.River(Zone.Meadow m5, Zone.River n1, Zone.Meadow m6)
                        when s2 instanceof TileSide.River(Zone.Meadow m7, Zone.River n2, Zone.Meadow m8) ->
                {
                    riverBuilder.union(n1, n2);
                    meadowBuilder.union(m5, m8);
                    meadowBuilder.union(m6, m7);
                    waterBuilder.union(n1,n2);
                }
                default -> throw new IllegalArgumentException();
            }
        }

        /**
         * Efface tous les cueilleurs d'une zone de forêt spécifique dans les ZonePartitions en cours de construction.
         *
         * @param forest La zone de forêt à partir de laquelle effacer les cueilleurs.
         */
        public void clearGatherers(Area<Zone.Forest> forest) {
            forestBuilder.removeAllOccupantsOf(forest);

        }
        /**
         * Efface tous les pêcheurs d'une zone de rivière spécifique dans les ZonePartitions en cours de construction.
         *
         * @param river La zone de rivière à partir de laquelle effacer les pêcheurs.
         */

        public void clearFishers(Area<Zone.River> river) {
            riverBuilder.removeAllOccupantsOf(river);

        }

        /**
         * Construit et renvoie l'objet ZonePartitions finalisé.
         *
         * @return L'objet ZonePartitions finalisé.
         */

        public ZonePartitions build() {
            return new ZonePartitions(forestBuilder.build(), meadowBuilder.build(),
                    riverBuilder.build(), waterBuilder.build());
        }
    }


}
