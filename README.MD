Bonjour les experts en QoSmultilabelswitchingmultilayervpnpolyintersessionMPBGPOSPFRIP.




Bandwitdth Broker (BB) -> Routeur
  Server Netcat sur le routeur en écoute constante avec l'argument -e pour permettre l'execution de commandes de shaping ( Fonctionnel, c'était chaud )
  Client Netcat sur le BB.


Proxy SIP -> BB
  Server java à implémenter sur le BB pour réceptionner les reqûetes SIP.
  Modifier le code du Proxy SIP pour envoyer des structures de données (INVITE,RINGING, etc...) qu'on aura déterminée.

Softphone <-> Proxy SIP
  Rien à faire pour cette connexion.



