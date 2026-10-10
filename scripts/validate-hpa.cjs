// Validate HPA metrics and deployment resource requests without a running cluster.
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const yaml = require("js-yaml");
const root = process.argv[2] || "k8s";
for (const prefix of ["backend", "frontend"]) {
  const read = (suffix) => yaml.load(fs.readFileSync(path.join(root, prefix + suffix), "utf8"));
  const hpa = read("-hpa.yaml");
  const deployment = read("-deployment.yaml");
  const spec = hpa.spec;
  assert.equal(hpa.apiVersion, "autoscaling/v2");
  assert.deepEqual(spec.scaleTargetRef, {
    apiVersion: "apps/v1", kind: "Deployment", name: deployment.metadata.name
  });
  assert.equal(hpa.metadata.namespace, deployment.metadata.namespace);
  assert.ok(spec.minReplicas >= 1 && spec.maxReplicas >= spec.minReplicas);
  const metrics = new Map(spec.metrics.filter(m => m.type === "Resource")
    .map(m => [m.resource.name, m.resource.target]));
  for (const resource of ["cpu", "memory"]) {
    const target = metrics.get(resource);
    assert.ok(target, prefix + ": missing " + resource + " metric");
    assert.equal(target.type, "Utilization");
    assert.ok(target.averageUtilization > 0);
    for (const container of deployment.spec.template.spec.containers) {
      const request = container.resources?.requests?.[resource];
      assert.ok(request && parseFloat(request) > 0, prefix + ": missing " + resource + " request");
    }
  }
  console.log(prefix + ": HPA metrics, deployment target and resource requests validated");
}
