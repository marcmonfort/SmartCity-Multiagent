/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.upc.edu.Behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import java.util.Random;

/**
 * @author igomez
 */
public class VehiculoAgent extends Agent {

    int pos_x = 0;
    int pos_y = 0;

    int direccion = 0;

    int velocidad = 0; // velocidad 1 o 0
    //int aceleracion = 1;

    int carril_fin_x = 2;
    int carril_fin_y = 0; 

    int[] carril_inter = {20,30};

    int dist_next_obstacle = 0; //-1 == infinite

    boolean all_green = true;

    int iter = 3;

    int elID;



    public class VehiculoTickerBehaviour extends TickerBehaviour {

        ACLMessage msg;

        public VehiculoTickerBehaviour(Agent a, long period) {
            super(a, period);
        }

        /* public void onStart() {
            msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID("Termostato", AID.ISLOCALNAME));
            msg.setSender(getAID());
        } */

        /* public int onEnd() {
            System.out.println("Bye..");
            return 0;
        } */

        public void onTick() {
            // mirar si se llega al final del carril
            boolean fin_calle = false;

            switch(direccion) {
                case 0: //right
                    pos_x += velocidad; 
                    break;
                case 1: //left
                    pos_x -= velocidad; 
                    break;
                case 2: //up
                    pos_y += velocidad; 
                    break;
                case 3: //down
                    pos_y -= velocidad; 
                    break;
            }
            if (pos_x == carril_fin_x && pos_y == carril_fin_y){
                velocidad = 0;
                // preguntar siguiente carretera
            }
            if (dist_next_obstacle == 1) {
                velocidad = 0;
            }

            
            --dist_next_obstacle; // reducimos distancia al objeto mas cercano
            
            // Establece posicion y recibe next obstacle en cada tick
            if (true) {
                final DFAgentDescription desc = new DFAgentDescription();
                final ServiceDescription sdesc = new ServiceDescription();
                sdesc.setType("Entorno");
                desc.addServices(sdesc);
                try {
                    final DFAgentDescription[] environments = DFService.search(VehiculoAgent.this, getDefaultDF(), desc, new SearchConstraints());
                    final AID environment = environments[0].getName();
                    final ACLMessage aclMessage = new ACLMessage(ACLMessage.QUERY_IF);

                    aclMessage.setProtocol(FIPANames.InteractionProtocol.FIPA_QUERY);
                    aclMessage.setSender(VehiculoAgent.this.getAID());
                    aclMessage.addReceiver(environment);
                    aclMessage.setContent(elID + "," + pos_x + "," + pos_y + "," + velocidad + "," + direccion);

                    myAgent.addBehaviour(new AchieveREInitiator(myAgent, aclMessage) {
                        //@override???
                        protected void handleInform(ACLMessage inform) {
                            //double t = Double.parseDouble(inform.getContent());
                            System.out.println(VehiculoAgent.this.getName() + " == " + inform.getContent()); //
                            
                        }
                    });
                    
                    
                    //VehiculoAgent.this.send(aclMessage);
                    
                    System.out.println("ha superado el send!");
                    iter = 4;
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
            //--iter;


            //velocidad += aceleracion;
            //System.out.println("Vehiculo: Posicion=("+pos_x+", "+pos_y+"), Velocidad="+velocidad+", Dirección="+direccion);


            //System.out.println("hola");
            /* if (true) { //request avanzar
                final DFAgentDescription desc = new DFAgentDescription();
                final ServiceDescription sdesc = new ServiceDescription();
                sdesc.setType("Entorno");
                desc.addServices(sdesc);
                try {
                    final DFAgentDescription[] environments = DFService.search(VehiculoAgent.this, getDefaultDF(), desc, new SearchConstraints());
                    final AID environment = environments[0].getName();
                    final ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                    aclMessage.setSender(VehiculoAgent.this.getAID());
                    aclMessage.addReceiver(environment);
                    aclMessage.setContent(pos_x + "," + pos_y + "," + velocidad + "," + direccion);
                    VehiculoAgent.this.send(aclMessage);
                    
                    System.out.println("ha superado el send!");
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            } */

            // repetido
            /* if (true) { //query algun obstaculo???
                final DFAgentDescription desc = new DFAgentDescription();
                final ServiceDescription sdesc = new ServiceDescription();
                sdesc.setType("Entorno");
                desc.addServices(sdesc);
                try {
                    final DFAgentDescription[] environments = DFService.search(VehiculoAgent.this, getDefaultDF(), desc, new SearchConstraints());
                    final AID environment = environments[0].getName();
                    final ACLMessage aclMessage = new ACLMessage(ACLMessage.QUERY_IF);
                    aclMessage.setSender(VehiculoAgent.this.getAID());
                    aclMessage.addReceiver(environment);
                    aclMessage.setContent("obstacle");
                    VehiculoAgent.this.send(aclMessage);
                    
                    System.out.println("hay obstaculo?");
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            } */


        }

    }

    protected void setup() {
        Object[] args = getArguments();
        elID = Integer.parseInt((String) args[0]);
        System.out.println(elID);

        // REGISTRO DF
        final DFAgentDescription desc = new DFAgentDescription();
        desc.setName(getAID());

        final ServiceDescription sdesc = new ServiceDescription();
        sdesc.setName("Vehiculo");
        sdesc.setType("Vehiculo");
        desc.addServices(sdesc);

        try {
            DFService.register(this, getDefaultDF(), desc);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        // FIN REGISTRO DF

        VehiculoTickerBehaviour b = new VehiculoTickerBehaviour(this, 3000);
        this.addBehaviour(b);
    }
}