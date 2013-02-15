package oving2;

import jade.lang.acl.ACLMessage;
import jade.util.leap.Collection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import saga.model.Loc;
import saga.model.map.BaseMap;
import tdt4280.MapRepresentation;
import tdt4280.agents.SagaAgent;
import jade.lang.acl.MessageTemplate;

public class LabyrinthAgent extends SagaAgent {

	/**
	 * 0 = ingen info om map, 1 = har vore, kan gå, 2 = blokkert/stein, 42 = mål
	 */
	private Loc firstBotsStartPosition = null;
	static int count = 0;
	int id;
	int[][] mapRepresentation = new int[BaseMap.DEFAULT_HEIGHT][BaseMap.DEFAULT_WIDTH];
	int x, y;
	int possibleMove = 0;
	boolean returning = false;
	ArrayList<Integer> moves = new ArrayList<>();
	boolean gameover = false;

	public void planNextAction() {
		if (getPosition().y == 0 && getPosition().x == 0 && id != 0){
			gameover();
		}
		if(gameover){
			gameover();
		}
		if (moves.isEmpty() && id == 0 && getPosition() != null) {
			moves.add(getPosition().x);
			moves.add(getPosition().y);
		}
		if (getPosition() == null) {
			return;
		}
		if (id == 0) {
			if (getPosition().y == 0 && getPosition().x == 0) {
				MapRepresentation map = see();
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				Iterator iter = map.getBuddies().iterator();
				while (iter.hasNext()) {
					String name = (String) iter.next();
					msg.addReceiver(map.getAID(name));
				}
				try {
					moves.add(possibleMove);
					msg.setContent(Arrays.toString(moves.toArray()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				send(msg);
				move(SOUTH);
				doWait(1000);
				gameover=true;
			}
			didWeMove(possibleMove);
			returning = false;
			percive();
			possibleMove = getAPossibleMove();
			move(possibleMove);
		} else {
			if (moves.isEmpty()) {
				MessageTemplate m1 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				ACLMessage message = blockingReceive(m1);
				if (message != null) {
					System.out.println(message.toString());
					System.out.println(message.getContent());
					Pair<Loc, ArrayList<Integer>> processedMessage = process(message.getContent());
					firstBotsStartPosition = processedMessage.x;
					moves = processedMessage.y;
					System.out.println(moves.toString());
				}
			} else if (!moves.isEmpty() && firstBotsStartPosition != null) {
				findStart();
			} else if (!moves.isEmpty() && firstBotsStartPosition == null) {
				move(moves.remove(0));
			}
		}
	}

	public void setupAgent() {
		id = count++;
		setImageIndex(4);
	}

	public Loc startLoc() {
		return new Loc((int) (Math.random() * 4), BaseMap.DEFAULT_HEIGHT - 1);
	}

	private void percive() {
		x = getPosition().x;
		y = getPosition().y;
		if (mapRepresentation[x][y] == 0)
			mapRepresentation[x][y] = 1;
	}

	private int getAPossibleMove() {
		if (y > 0 && mapRepresentation[x][y - 1] == 0) {
			return 0;
		} else if (x > 0 && mapRepresentation[x - 1][y] == 0) {
			return 3;
		} else if (x < BaseMap.DEFAULT_WIDTH - 1 && mapRepresentation[x + 1][y] == 0) {
			return 1;
		} else if (y < BaseMap.DEFAULT_HEIGHT - 1 && mapRepresentation[x][y + 1] == 0) {
			return 2;
		} else {
			mapRepresentation[x][y] = 2;
			returning = true;
			return getOppositeMove(moves.remove(moves.size() - 1));
		}
	}

	private void didWeMove(int direction) {
		if (x == getPosition().x && y == getPosition().y) {
			Pair dir = trans(direction);
			if (x + (int) dir.x >= 0 && x + (int) dir.x < BaseMap.DEFAULT_WIDTH && y + (int) dir.y >= 0 && y + (int) dir.y < BaseMap.DEFAULT_HEIGHT)
				mapRepresentation[x + (int) dir.x][(int) dir.y + y] = 2;
		} else if (!returning) {
			moves.add(direction);
		}
	}

	private int getOppositeMove(int move) {
		switch (move) {
		case 0:
			return 2;
		case 1:
			return 3;
		case 2:
			return 0;
		case 3:
			return 1;
		}
		return 0;
	}

	private void printBoard() {
		for (int i = 0; i < BaseMap.DEFAULT_HEIGHT; i++) {
			for (int j = 0; j < BaseMap.DEFAULT_WIDTH; j++) {
				System.out.print(mapRepresentation[j][i]);
			}
			System.out.println();
		}
	}

	/**
	 * Transforms the fucking direction
	 */
	private Pair<Integer, Integer> trans(int i) {
		if (i == 0) {
			return new Pair(0, -1);
		} else if (i == 1) {
			return new Pair(1, 0);
		} else if (i == 2) {
			return new Pair(0, 1);
		} else {
			return new Pair(-1, 0);
		}
	}

	private Pair<Loc, ArrayList<Integer>> process(String message) {
		message = message.substring(1, message.length() - 1);
		String[] temp = message.replaceAll(" ", "").split(",");
		Loc startPos = new Loc(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
		Integer[] temp2 = new Integer[temp.length - 2];
		for (int i = 2; i < temp.length; i++) {
			temp2[i - 2] = Integer.parseInt(temp[i]);
		}
		return new Pair<>(startPos, new ArrayList<Integer>(Arrays.asList(temp2)));
	}

	public void findStart() {
		Loc loc = getPosition();
		if (firstBotsStartPosition.x - loc.x != 0) {
			if (firstBotsStartPosition.x - loc.x > 0) {
				move(EAST);
			} else {
				move(WEST);
			}
		} else if (firstBotsStartPosition.y - loc.y != 0) {
			if (firstBotsStartPosition.y - loc.x > 0) {
				move(SOUTH);
			} else {
				move(NORTH);
			}
		} else {
			firstBotsStartPosition = null;
		}
	}

	private class Pair<S, T> {
		public S x;
		public T y;

		public Pair(S first, T second) {
			this.x = first;
			this.y = second;
		}
	}
}