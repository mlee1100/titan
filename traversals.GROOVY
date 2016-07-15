// load data script
load /home/ec2-user/testspace/load-coreg.GROOVY

// good sample email
e = 'aaltjen@aol.com'
// distinct full_address with counts from that email
g.V('email',e).outE('lives').inV.dedup().transform{[it.full_address,it.inE.as('o').outV.has('email',e).back('o').count()]}
// distinct full_address with counts from that email sorted by counts desc
g.V('email',e).outE('lives').inV.dedup().transform{[it.full_address,it.inE.as('o').outV.has('email',e).back('o').count()]}.order{it.b[1] <=> it.a[1]}
// get most occuring address_full from email
g.V('email',e).outE('lives').inV.dedup().transform{[it.full_address,it.inE.as('o').outV.has('email',e).back('o').count()]}.order{it.b[1] <=> it.a[1]}.first()[0]