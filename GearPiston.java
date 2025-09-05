import java.util.*;
import java.io.*;

public class GearPiston {
    static DirectedWeightedGraphAdjacency<City> network = new DirectedWeightedGraphAdjacency<City>();
    static DirectedWeightedGraphAdjacency<City> optimizedNetwork = new DirectedWeightedGraphAdjacency<City>();  

    static class City{
        private String name; 
        private int x; 
        private int y; 
        private int population; 

        public City(String name, int x, int y, int population){
            this.name = name; 
            this.x = x; 
            this.y = y; 
            this.population = population; 
        }

        public String getName() {
            return name;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getPopulation() {
            return population;
        }
    }

    public static void readFile(String inputFile) {
        try {
            File cFile = new File(inputFile);
            FileReader fileReader = new FileReader(cFile);
            BufferedReader reader = new BufferedReader(fileReader);

            int numCities = Integer.parseInt(reader.readLine());
            for (int i = 0; i < numCities; i++) {
                String line = reader.readLine(); 
                String[] parts = line.split(" "); 
                int population = Integer.parseInt(parts[0]); 
                int x = Integer.parseInt(parts[1]); 
                int y = Integer.parseInt(parts[2]); 
                String name = parts[3]; 
                City newCity = new City(name, x, y, population); 
                network.addVertex(newCity); 
            }

            reader.close();
            fileReader.close();
        } catch (IOException e) {
            System.err.println("Read Failed.");
            e.printStackTrace();
        }
    }

    public static double computeWeight(City from, City to){
        double result; 

        int x1 = from.getX(); 
        int x2 = to.getX(); 
        int y1 = from.getY(); 
        int y2 = to.getY(); 
        int popSum = from.getPopulation() + to.getPopulation(); 

        double firstTerm = Math.pow(x2 - x1, 2); 
        double secondTerm = Math.pow(y2 - y1, 2); 
        double eDist = Math.sqrt(firstTerm + secondTerm); 
        result = eDist / popSum; 

        return result; 
    }

    public static void fullyConnectGraph(){
        for(City fromCity : network.getVertices()){
            for(City toCity : network.getVertices()){
                if(toCity.getName() != fromCity.getName() && toCity.getPopulation() != fromCity.getPopulation()){
                    double weight = computeWeight(fromCity, toCity); 
                    network.addEdge(fromCity, toCity, weight); 
                }
            }
        }
    }

    static class primCity{
        City from; 
        City to; 

        public primCity(City from, City to){
            this.from = from; 
            this.to = to; 
        }
    }

    public static void optimizeNetwork(){
        Set<City> setOfCities = network.getVertices();
        Iterator<City> itr = setOfCities.iterator(); 
        City startCity = itr.next(); 


        ExplicitHeapPriorityQueue<Double, primCity> pq = new ExplicitHeapPriorityQueue<Double, primCity>(); 
        Set<City> visited = new HashSet<City>();
        primCity startElement = new primCity(null, startCity);  
        pq.add(0, startElement); 
        
        while(!pq.isEmpty() && !visited.equals(network.getVertices())){ 
            primCity curr = pq.remove(); 
            City v = curr.to;
            if(!visited.contains(v)){
                visited.add(v);
                if(curr.from != null){
                    optimizedNetwork.addVertex(curr.from); 
                    optimizedNetwork.addVertex(v);
                    optimizedNetwork.addEdge(curr.from, v, network.getEdge(curr.from, v).getWeight());    
                }
                for(City w: network.getAdjacents(v)){ 
                    if(!visited.contains(w)){   
                        primCity pc = new primCity(v, w); 
                        pq.add(network.getEdge(v, w).getWeight(), pc); 
                    }
                }
            }
        }
    }

    public static void generateSVG(String outputFileName){
        try {
            FileWriter writer = new FileWriter(outputFileName);
            String header = "<svg  viewBox=\"0 0 3000 2000\" version=\"1.1\"     baseProfile=\"full\"     xmlns=\"http://www.w3.org/2000/svg\"     xmlns:xlink=\"http://www.w3.org/1999/xlink\"     xmlns:ev=\"http://www.w3.org/2001/xml-events\">";
            writer.write(header); 
            
            for(DirectedWeightedEdge<City> edge : optimizedNetwork.getEdges()){
                int x1 = edge.getFrom().getX(); 
                int y1 = edge.getFrom().getY(); 

                int x2 = edge.getTo().getX(); 
                int y2 = edge.getTo().getY(); 

                String svgLine = String.format("<line stroke=\"black\" x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\"/>", x1, y1, x2, y2);
                writer.write(svgLine);  
            }
             
            for(City city : optimizedNetwork.getVertices()){
                int x = city.getX(); 
                int y = city.getY(); 
                int pop = city.getPopulation();  
                double radius = (pop * 10) / 3_000_000; 

                String svgLine = String.format("<circle cx=\"%d\" cy=\"%d\" r=\"%f\" fill=\"red\"/>", x, y, radius); 
                writer.write(svgLine);  
            }
            
            String closer = "</svg>"; 
            writer.write(closer); 
            
            writer.close();
        } catch (IOException e) {
            System.out.println("Error generating svg");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
 
        if(args.length < 2){
            System.err.println("Ussage: [C-FILE-NAME] [OUTPUT-FILE-NAME]"); 
            System.exit(1); 
        }
    
        String inputFile = args[0]; 
        String outputFileName = args[1]; 

        readFile(inputFile);  

        fullyConnectGraph();

        optimizeNetwork(); 

        generateSVG(outputFileName); 
    }
}
