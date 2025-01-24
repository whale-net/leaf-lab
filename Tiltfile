load('ext://namespace', 'namespace_create')
# appending '-dev' just in case this is ever ran from prod cluster
namespace = 'leaf-lab-dev'
namespace_create(namespace)

load('ext://dotenv', 'dotenv')
# load env vars from .env
dotenv()

docker_build(
    'leaf-lab',
    context='.'
)

# create fcm app
k8s_yaml(
    helm(
        'charts/leaf-lab',
        name='leaf-lab-app',
        namespace=namespace,
        # using set instead of values, for now? for ever?
        #values=['path/to'],
        set=[
            'image.name=leaf-lab',
            'namespace={}'.format(namespace),
            'db.url={}'.format(os.getenv('DB_URL')),
            'db.user={}'.format(os.getenv('DB_USER')),
            'db.pass={}'.format(os.getenv('DB_PASS'))
        ]
    )
)

# forward localhost:8080 to the pod
k8s_resource(
    workload='leaf-lab-deployment',
    port_forwards='8080:8080'
)