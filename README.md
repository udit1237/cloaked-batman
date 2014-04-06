cloaked-batman
==============

Determine the robustness of network of US airports

Working Title: 
US Air Traffic Network Congestion and Ability to Cope with Disaster / Centrality

Dataset: 

Airline Traffic Data: http://stat-computing.org/dataexpo/2009/the-data.html
Airline traffic data from 1987 - 2008
We plan on using the data to collect the number of flights into and out of each airport
We will use Origin, Destination, DayofWeek, Month, Year, 

Aiport Supplemental Dataset: http://stat-computing.org/dataexpo/2009/supplemental-data.html
Airport name, location, and IATA code
This data will be used to translate the IATA code for airports used in the first dataset, as well as provide longitude and latitude for to help determine where flights might be diverted if nodes were taken out of the graph.
We will use lat, long, code, and name.

All data collected by Research and Innovative Technology Administration


Goals:

	We are attempting to recreate the US airport network, and simulate the effects on air traffic congestion if heavily used nodes, or networks of nodes, were to be taken offline. We will be calculating the centrality of each node and calculate that how does the removal of a node or network would affect the centrality of the system.



Milestones:

Descriptive measures: Calculation of descriptive measures like mean and mode. In our case mean and mode would be the number of flights per day for each airport which we will assign as the edges in our network.  This must be derived from the dataset of all flights to create a dataset of airports.

Create Airport/Flight network graph for a particular time range (days or months) where the nodes represents the airports and the edges represents the number of incoming and outgoing flight for the selected time range.

Centrality/Rank calculation: Calculate the importance of nodes (airports) in terms of congestion using an a ranking algorithm. The centrality/rank calculation depends upon the sum of the incoming flights and the outgoing flights, shortest path and some other factors. See wikipedia http://en.wikipedia.org/wiki/Centrality

Removal of Nodes:  Remove nodes and divert incoming and outgoing flights to nearby geographical airports.  Check how centrality changes after a node is removed. 

Summary of results/Documentation: Summarizing results, creating visualization models and documenting results.

Determine airports whose congestion influences the delays. (If time permits). Using The correlation between various centrality indices and other network analytic characteristics (such as clustering coefficient) and delayed flights.

